export function required(value, label) {
  if (value === null || value === undefined || String(value).trim() === '') {
    throw new Error(`${label}不能为空`)
  }
}

export function minLength(value, length, label) {
  if (!value || String(value).length < length) {
    throw new Error(`${label}不能少于${length}位`)
  }
}

export function positiveNumber(value, label) {
  if (Number(value) <= 0) {
    throw new Error(`${label}必须大于0`)
  }
}

export function phoneOptional(value) {
  if (!value) return
  if (!/^1\d{10}$/.test(String(value))) {
    throw new Error('手机号必须为11位大陆手机号')
  }
}

export function emailOptional(value) {
  if (!value) return
  if (!/^\S+@\S+\.\S+$/.test(String(value))) {
    throw new Error('邮箱格式不正确')
  }
}

export function qqOptional(value) {
  if (!value) return
  if (!/^[1-9]\d{4,11}$/.test(String(value))) {
    throw new Error('QQ号格式不正确')
  }
}

export function wechatOptional(value) {
  if (!value) return
  if (!/^[a-zA-Z][-_a-zA-Z0-9]{5,19}$/.test(String(value))) {
    throw new Error('微信号格式不正确，需以字母开头，长度6-20位')
  }
}

export function maxLengthOptional(value, length, label) {
  if (!value) return
  if (String(value).length > length) {
    throw new Error(`${label}不能超过${length}个字符`)
  }
}