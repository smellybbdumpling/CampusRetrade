package com.campus.trade.service;

import com.campus.trade.dto.CategoryVO;

import java.util.List;

public interface CategoryService {

    List<CategoryVO> listEnabledCategories();
}