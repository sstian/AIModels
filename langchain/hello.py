
# 控制台输出编码，解决中文乱码
import sys
import io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# 导包
import dotenv
import os
from langchain_openai import ChatOpenAI

# 加载当前目录下的.env文件
dotenv.load_dotenv()

os.environ['OPENAI_API_KEY'] = os.getenv("OPENAI_API_KEY")
os.environ['OPENAI_BASE_URL'] = os.getenv("OPENAI_BASE_URL")

# 创建大模型实例
# # 调用非对话模型
# llm = OpenAI()
# response = llm.invoke("写一首关于春天的诗")

# 调用对话模型
llm = ChatOpenAI(model="deepseek-chat")
response = llm.invoke("什么是大模型？")

# # 调用嵌入模型
# embeddings_model = OpenAIEmbeddings(model="text-embedding-ada-002")
# response = embeddings_model.embed_query('我是文档中的数据')

print(response)
