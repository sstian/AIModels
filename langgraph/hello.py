from typing import TypedDict
from langgraph.graph import StateGraph, START, END

# 1. 定义 State
# TypedDict: 属于Python标准库typing模块的一部分，仅提供静态类型检查，运行时不执行验证
# Pydantic: 第三方库，需要单独安装，提供运行时数据验证和序列化功能
class HelloState(TypedDict):
    name: str
    greeting: str

# 2. 定义节点
def greet(state: HelloState) -> dict:
    name = state["name"]
    return {"greeting": f"你好，{name}！"}

def add_emoji(state: HelloState) -> dict:
    greeting = state["greeting"]
    return {"greeting": greeting}

# 3. 构建图
graph = StateGraph(HelloState)

graph.add_node("greet", greet)
graph.add_node("add_emoji", add_emoji)

graph.add_edge(START, "greet")
graph.add_edge("greet", "add_emoji")
graph.add_edge("add_emoji", END)

# 4. 编译
app = graph.compile()

# 5. 运行
result = app.invoke({"name": "张三"})
print(result["greeting"])  # 输出：你好，张三！


# 使用 Graphviz 渲染（Colab 最稳定的方案）
from IPython.display import Image, display

try:
    display(Image(app.get_graph(xray=True).draw_png()))
except Exception as e:
    print(f"Graphviz 渲染失败: {e}")
    print("\n使用 Mermaid 文本方式显示:")
    print(app.get_graph(xray=True).draw_mermaid())
