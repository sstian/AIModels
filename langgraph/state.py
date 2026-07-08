
""" 
基本的 State 定义 
"""
from typing import TypedDict

class BasicState(TypedDict):
    user_input: str
    response: str
    count: int


"""
高级的 State 定义 

使用 Annotated 和 Reducer：
- Annotated 是 Python 标准 typing 机制的一部分，用于给类型附加元数据。Python documentation
- 在 LangGraph 的场景下，它的元数据就是 reducer 函数（如 add、add_messages、lambda）。
- 类型检查器会看到基础类型（比如 int、list），而框架会看到元数据并以此驱动状态合并逻辑。
"""
from typing import TypedDict, Annotated
from operator import add
from langgraph.graph.message import add_messages

class AdvancedState(TypedDict):
    # 普通字段：直接替换
    user_name: str
    session_id: str

    # 使用 add reducer：累加
    # 一个通用的 reducer 函数，对基础数据做“相加/累加”
    total_tokens: Annotated[int, add]

    # 使用 add_messages：消息列表管理
    # 一个专门处理消息（message lists）的 reducer
    # 对 消息列表（messages） 做追加 & 管理
    messages: Annotated[list, add_messages]

    # 自定义 reducer
    tags: Annotated[list, lambda old, new: list(set(old + new))]
