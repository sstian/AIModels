
from langchain_core.tools import tool
from langchain_core.messages import AIMessage
from langgraph.prebuild import ToolNode

# 工具定义
@tool
def get_weather(location: str):
  """调用此函数获取当前天气"""
  if location.lower() in ["北京", "深圳"]:
    return "现在是20度，有雾。"
  else:
    return "现在是10度，晴朗。"

@tool
def get_coolest_cities():
  """获取最冷城市列表"""
  return "哈尔滨，北京"

tools = [get_weather, get_coolest_cities]
tool_node = ToolNode(tools)

# # 工具调用 - 手动
# message_with_single_tool_call = AIMessage(
#   content="",
#   tool_calls=[
#     {
#       "name": "get_weather",
#       "args": { "location": "北京" },
#       "id": "tool_call_id",
#       "type": "tool_call",
#     }
#   ],
# )
# tool_node.invoke({"message": [message_with_single_tool_call]})


from langchain_deepseek import ChatDeepSeek
# 工具绑定
model_with_tools = ChatDeepSeek(
  model="Pro/deepseek-ai/DeepSeek-V3",
  temperature=0,
  api_key=os.environ.get("DEEPSEEK_API_KEY"),
  base_url=os.environ.get("DEEPSEEK_API_BASE"),
).bind_tools(tools)

# 工具调用 - 自动
model_with_tools.invoke("深圳的天气如何？").tool_calls

# 工具执行，返回ToolMeesage
tool_node,invoke({"messages": [model_with_tools,invoke("深圳的天气如何？")]})


"""
在ReAct智能体中执行
"""
from langgraph.graph import StateGraph, MessageState, START, END

def should_continue(state: MessageState):
  messages = state[",essages"]
  last_message = messages[-1]
  if last_message.tool_calls:
    return "tools"
  return END

def call_model(state: MessageState):
  messages = state["messages"]
  response = model_with_tools.invoke(messages)
  return {"message": [response]}

workflow = StateGraph(MessageState)
workflow.add_node("agent", call_model)
workflow.add_node("tools", tool_node)

workflow.add_edge(START, "agent")
workflow.add_conditional_edges("agent", should_continue, ["tools", END])
workflow.add_edge("tools", "agent")

app = workflow.compile()

for chunk in app.stream({"messages": [("human", "深圳的天气如何？")]}, stream_mode="values"):
  chunk["messages"][-1].pretty_print()
