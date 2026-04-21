"""
LangChain调用本地大模型

第1步：下载并安装Ollama
第2步：在Ollama下部署大模型：deepseek-r1:7b
第3步：在LangChain中调用Ollama下的大模型

> NOT RUN!
"""

from langchain_core.messages import HumanMessage
from langchain_ollama import ChatOllama

# 此时调用的是本地的大模型。省略base_url、api-key
llm = ChatOllama(model = "deepseek-r1:7b")
# llm.invoke("你好，请介绍一下你自己！")

messages = [
    HumanMessage(content="你好，请介绍一下你自己！")
]
response = llm.invoke(messages)
print(response.content)
