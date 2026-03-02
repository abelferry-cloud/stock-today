#!/bin/bash
# Git Smudge Filter - 将占位符替换为真实 API Key
# 在 git checkout 时执行，从 secrets.env 读取真实值并还原

# 读取 secrets.env 文件
SECRETS_FILE="$(git rev-parse --show-toplevel)/scripts/secrets.env"

# 从标准输入读取内容
content=$(cat)

# 如果 secrets.env 存在，读取其中的真实值并替换占位符
if [ -f "$SECRETS_FILE" ]; then
    # 读取 secrets.env 中的值

    # DeepSeek API Keys
    key1=$(grep "^DEEPSEEK_API_KEY_1=" "$SECRETS_FILE" | cut -d'=' -f2)
    key2=$(grep "^DEEPSEEK_API_KEY_2=" "$SECRETS_FILE" | cut -d'=' -f2)
    key3=$(grep "^DEEPSEEK_API_KEY_3=" "$SECRETS_FILE" | cut -d'=' -f2)

    # 阿里云百炼 API Key
    aliyun_key=$(grep "^ALIYUN_DASHSCOPE_API_KEY=" "$SECRETS_FILE" | cut -d'=' -f2)

    # Pinecone API Key
    pinecone_key=$(grep "^PINECONE_API_KEY=" "$SECRETS_FILE" | cut -d'=' -f2)

    # 数据库密码
    db_username=$(grep "^DB_USERNAME=" "$SECRETS_FILE" | cut -d'=' -f2)
    db_password=$(grep "^DB_PASSWORD=" "$SECRETS_FILE" | cut -d'=' -f2)

    # RabbitMQ 密码
    rabbitmq_password=$(grep "^RABBITMQ_PASSWORD=" "$SECRETS_FILE" | cut -d'=' -f2)
    crawler_rabbitmq_password=$(grep "^CRAWLER_RABBITMQ_PASSWORD=" "$SECRETS_FILE" | cut -d'=' -f2)

    # 替换占位符为真实值 (使用 | 作为分隔符)
    # DeepSeek Keys
    if [ -n "$key1" ]; then
        content=$(echo "$content" | sed "s|YOUR_DEEPSEEK_API_KEY_1|$key1|g")
    fi
    if [ -n "$key2" ]; then
        content=$(echo "$content" | sed "s|YOUR_DEEPSEEK_API_KEY_2|$key2|g")
    fi
    if [ -n "$key3" ]; then
        content=$(echo "$content" | sed "s|YOUR_DEEPSEEK_API_KEY_3|$key3|g")
    fi

    # 阿里云百炼
    if [ -n "$aliyun_key" ]; then
        content=$(echo "$content" | sed "s|YOUR_ALIYUN_DASHSCOPE_API_KEY|$aliyun_key|g")
    fi

    # Pinecone
    if [ -n "$pinecone_key" ]; then
        content=$(echo "$content" | sed "s|YOUR_PINECONE_API_KEY|$pinecone_key|g")
    fi

    # 数据库
    if [ -n "$db_username" ]; then
        content=$(echo "$content" | sed "s|YOUR_DB_USERNAME|$db_username|g")
    fi
    if [ -n "$db_password" ]; then
        content=$(echo "$content" | sed "s|YOUR_DB_PASSWORD|$db_password|g")
    fi

    # RabbitMQ
    if [ -n "$rabbitmq_password" ]; then
        content=$(echo "$content" | sed "s|YOUR_RABBITMQ_PASSWORD|$rabbitmq_password|g")
    fi
    if [ -n "$crawler_rabbitmq_password" ]; then
        content=$(echo "$content" | sed "s|YOUR_CRAWLER_RABBITMQ_PASSWORD|$crawler_rabbitmq_password|g")
    fi
fi

# 输出到标准输出
echo "$content"
