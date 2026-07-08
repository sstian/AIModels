from typing import TypedDict, Literal
from langgraph.graph import StateGraph, START, END

class State(TypedDict):
    score: float
    decision: str

def evaluate(state: State) -> dict:
    """评估并打分"""
    return {"score": 0.75}

def route_by_score(state: State) -> Literal["high", "medium", "low"]:
    """根据分数路由"""
    score = state["score"]
    if score > 0.8:
        return "high"
    elif score > 0.5:
        return "medium"
    else:
        return "low"

def handle_high(state: State) -> dict:
    return {"decision": "自动通过"}

def handle_medium(state: State) -> dict:
    return {"decision": "人工审核"}

def handle_low(state: State) -> dict:
    return {"decision": "自动拒绝"}

# 构建图
graph = StateGraph(State)
graph.add_node("evaluate", evaluate)
graph.add_node("high", handle_high)
graph.add_node("medium", handle_medium)
graph.add_node("low", handle_low)

graph.add_edge(START, "evaluate")
# 条件路由
graph.add_conditional_edges(
    "evaluate",
    route_by_score,
    {
        "high": "high",
        "medium": "medium",
        "low": "low"
    }
)

for node in ["high", "medium", "low"]:
    graph.add_edge(node, END)

app = graph.compile()
display_graph(app)
