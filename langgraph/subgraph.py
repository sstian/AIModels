from typing import TypedDict
from langgraph.graph import StateGraph, START, END

# === 子图 State ===
class SubGraphState(TypedDict):
    input_value: int
    result: int

# === 子图节点 ===
def sub_step1(state: SubGraphState) -> dict:
    """子图步骤 1"""
    return {"result": state["input_value"] * 2}

def sub_step2(state: SubGraphState) -> dict:
    """子图步骤 2"""
    return {"result": state["result"] + 10}

# === 创建子图 ===
sub_graph = StateGraph(SubGraphState)
sub_graph.add_node("step1", sub_step1)
sub_graph.add_node("step2", sub_step2)
sub_graph.add_edge(START, "step1")
sub_graph.add_edge("step1", "step2")
sub_graph.add_edge("step2", END)

# 编译子图
sub_graph_compiled = sub_graph.compile()

# === 主图 State ===
class MainState(TypedDict):
    number: int
    final_result: int

# === 主图节点（使用子图）===
def prepare_input(state: MainState) -> dict:
    """准备输入"""
    return {"number": state["number"]}

def use_subgraph(state: MainState) -> dict:
    """使用子图"""
    # 调用子图
    result = sub_graph_compiled.invoke({
        "input_value": state["number"],
        "result": 0
    })

    return {"final_result": result["result"]}

# === 创建主图 ===
main_graph = StateGraph(MainState)
main_graph.add_node("prepare", prepare_input)
main_graph.add_node("process", use_subgraph)

main_graph.add_edge(START, "prepare")
main_graph.add_edge("prepare", "process")
main_graph.add_edge("process", END)

main_app = main_graph.compile()
display_graph(main_app)
# === 测试 ===
result = main_app.invoke({"number": 5, "final_result": 0})
print(f"输入: 5")
print(f"子图处理: 5 * 2 + 10 = {result['final_result']}")  # 20
