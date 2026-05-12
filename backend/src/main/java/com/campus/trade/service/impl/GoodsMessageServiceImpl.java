package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.BusinessException;
import com.campus.trade.common.PageResult;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.AdminGoodsMessageQueryDTO;
import com.campus.trade.dto.GoodsMessageSaveRequest;
import com.campus.trade.dto.GoodsMessageVO;
import com.campus.trade.entity.Goods;
import com.campus.trade.entity.GoodsMessage;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.GoodsMapper;
import com.campus.trade.mapper.GoodsMessageMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.GoodsMessageService;
import com.campus.trade.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoodsMessageServiceImpl implements GoodsMessageService {

    private final GoodsMessageMapper goodsMessageMapper;
    private final GoodsMapper goodsMapper;
    private final UserMapper userMapper;
    private final UserNotificationService userNotificationService;

    @Override
    public List<GoodsMessageVO> listGoodsMessages(Long goodsId, Long currentUserId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        List<GoodsMessage> messages = goodsMessageMapper.selectList(new LambdaQueryWrapper<GoodsMessage>()
                .eq(GoodsMessage::getGoodsId, goodsId)
                .orderByAsc(GoodsMessage::getCreateTime, GoodsMessage::getId));
        return buildMessageTree(messages);
    }

    @Override
    @Transactional
    public void saveGoodsMessage(Long userId, Long goodsId, GoodsMessageSaveRequest request) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        User user = userMapper.selectById(userId);
        if (user == null || !"NORMAL".equals(user.getStatus())) {
            throw new BusinessException("当前用户不可发送留言");
        }
        GoodsMessage parent = null;
        if (request.getParentId() != null) {
            parent = goodsMessageMapper.selectById(request.getParentId());
            if (parent == null || !goodsId.equals(parent.getGoodsId())) {
                throw new BusinessException("回复的留言不存在");
            }
            if (userId.equals(parent.getSenderId())) {
                throw new BusinessException("不能回复自己发送的留言");
            }
        } else if (userId.equals(goods.getSellerId())) {
            throw new BusinessException("卖家不能给自己的商品发起顶层留言");
        }

        GoodsMessage message = new GoodsMessage();
        message.setGoodsId(goodsId);
        message.setSenderId(userId);
        message.setParentId(parent == null ? null : parent.getId());
        message.setContent(request.getContent().trim());
        goodsMessageMapper.insert(message);

        if (parent == null) {
            userNotificationService.createNotification(
                    goods.getSellerId(),
                    "GOODS_MESSAGE",
                    "你的商品收到了新咨询",
                    user.getNickname() + " 给商品《" + goods.getTitle() + "》发来了一条新留言",
                    "/goods/" + goodsId
            );
        } else {
            userNotificationService.createNotification(
                    parent.getSenderId(),
                    "GOODS_REPLY",
                    "你收到了新的回复",
                    user.getNickname() + " 回复了你关于《" + goods.getTitle() + "》的留言",
                    "/goods/" + goodsId
            );
        }
    }

    @Override
    public PageResult<GoodsMessageVO> adminPageMessages(Long adminUserId, AdminGoodsMessageQueryDTO queryDTO) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);
        List<GoodsMessageVO> filtered = goodsMessageMapper.selectList(new LambdaQueryWrapper<GoodsMessage>()
                        .orderByDesc(GoodsMessage::getCreateTime, GoodsMessage::getId))
                .stream()
                .map(this::toMessageVO)
                .filter(item -> !StringUtils.hasText(queryDTO.getKeyword()) || item.getContent().contains(queryDTO.getKeyword()))
                .filter(item -> !StringUtils.hasText(queryDTO.getSenderUsername()) || (item.getSenderUsername() != null && item.getSenderUsername().contains(queryDTO.getSenderUsername())))
                .toList();

        int fromIndex = Math.max(0, (queryDTO.getPageNum() - 1) * queryDTO.getPageSize());
        int toIndex = Math.min(filtered.size(), fromIndex + queryDTO.getPageSize());
        List<GoodsMessageVO> records = fromIndex >= filtered.size() ? List.of() : filtered.subList(fromIndex, toIndex);
        return new PageResult<>((long) filtered.size(), records);
    }

    @Override
    @Transactional
    public void adminDeleteMessage(Long adminUserId, Long messageId) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);
        GoodsMessage message = goodsMessageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException("留言不存在");
        }
        goodsMessageMapper.delete(new LambdaQueryWrapper<GoodsMessage>()
                .eq(GoodsMessage::getId, messageId)
                .or()
                .eq(GoodsMessage::getParentId, messageId));
    }

    @Override
    @Transactional
    public void adminDeleteAllMessages(Long adminUserId) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);
        goodsMessageMapper.delete(new LambdaQueryWrapper<GoodsMessage>());
    }

    private List<GoodsMessageVO> buildMessageTree(List<GoodsMessage> messages) {
        Map<Long, GoodsMessageVO> voMap = new LinkedHashMap<>();
        for (GoodsMessage message : messages) {
            GoodsMessageVO vo = toMessageVO(message);
            vo.setReplies(new ArrayList<>());
            voMap.put(vo.getId(), vo);
        }
        List<GoodsMessageVO> roots = new ArrayList<>();
        for (GoodsMessage message : messages) {
            GoodsMessageVO current = voMap.get(message.getId());
            if (message.getParentId() == null) {
                roots.add(current);
            } else {
                GoodsMessageVO parent = voMap.get(message.getParentId());
                if (parent != null) {
                    parent.getReplies().add(current);
                }
            }
        }
        return roots;
    }

    private GoodsMessageVO toMessageVO(GoodsMessage message) {
        GoodsMessageVO vo = new GoodsMessageVO();
        vo.setId(message.getId());
        vo.setGoodsId(message.getGoodsId());
        vo.setSenderId(message.getSenderId());
        User sender = userMapper.selectById(message.getSenderId());
        vo.setSenderName(sender == null ? "未知用户" : sender.getNickname());
        vo.setSenderUsername(sender == null ? null : sender.getUsername());
        vo.setParentId(message.getParentId());
        vo.setContent(message.getContent());
        vo.setCreateTime(message.getCreateTime());
        return vo;
    }
}
