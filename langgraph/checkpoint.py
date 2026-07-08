"""
Persistence：Checkpoint 机制

执行流程：
START → Node A → [Checkpoint 1] → Node B → [Checkpoint 2] → Node C → [Checkpoint 3] → END

每个 Checkpoint 包含：
- 完整的 State 快照
- 节点执行历史
- 时间戳
- 父 Checkpoint 引用

"""

from langgraph.checkpoint.memory import MemorySaver
from langgraph.graph import StateGraph, START, END
from typing import TypedDict

class SimpleState(TypedDict):
    count: int

def increment(state: SimpleState) -> dict:
    return {"count": state.get("count", 0) + 1}

# 创建图
graph = StateGraph(SimpleState)
graph.add_node("increment", increment)
graph.add_edge(START, "increment")
graph.add_edge("increment", END)

# 使用内存存储：MemorySaver
checkpointer = MemorySaver()
app = graph.compile(checkpointer=checkpointer)

# 运行时指定 thread_id
config = {"configurable": {"thread_id": "thread-1"}}

# 第一次运行
result1 = app.invoke({"count": 0}, config)
print(result1)  # {"count": 1}

# 第二次运行（同一个 thread，状态会累加）
result2 = app.invoke({}, config)
print(result2)  # {"count": 2}

# 新的 thread（独立的状态）
config2 = {"configurable": {"thread_id": "thread-2"}}
result3 = app.invoke({"count": 0}, config2)
print(result3)  # {"count": 1}
