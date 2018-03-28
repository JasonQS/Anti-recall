# 正在重构 原项目不可用

撤回的消息会出现在列表

点进去可以查看详细上下文

如果有撤回的图片 需要点进去预览大图

单击撤回的地方显示已经查找到的消息

双击撤回的地方则是 如果显示错误 需要重新查找

```flow
st=>start: Start
op=>operation: Your Operation
cond=>condition: Yes or No?
e=>end
st->op->cond
cond(yes)->e
cond(no)->op
```