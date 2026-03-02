#!/bin/bash
# Git Clean Filter - 将真实 API Key 替换为占位符
# 在 git add 时执行，将敏感信息替换为占位符

# 读取 secrets.env 文件
SECRETS_FILE="$(git rev-parse --show-toplevel)/scripts/secrets.env"

# 从标准输入读取内容
content=$(cat)

# 如果 secrets.env 存在，读取其中的真实值并替换为占位符
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

    # 替换真实值为占位符 (使用 | 作为分隔符避免与 / 冲突)
    # DeepSeek Keys
    if [ -n "$key1" ]; then
        content=$(echo "$content" | sed "s|$key1|YOUR_DEEPSEEK_API_KEY_1|g")
    fi
    if [ -n "$key2" ]; then
        content=$(echo "$content" | sed "s|$key2|YOUR_DEEPSEEK_API_KEY_2|g")
    fi
    if [ -n "$key3" ]; then
        content=$(echo "$content" | sed "s|$key3|YOUR_DEEPSEEK_API_KEY_3|g")
    fi

    # 阿里云百炼
    if [ -n "$aliyun_key" ]; then
        content=$(echo "$content" | sed "s|$aliyun_key|YOUR_ALIYUN_DASHSCOPE_API_KEY|g")
    fi

    # Pinecone
    if [ -n "$pinecone_key" ]; then
        content=$(echo "$content" | sed "s|$pinecone_key|YOUR_PINECONE_API_KEY|g")
    fi

    # RabbitMQ - 使用上下文感知替换（先替换 RabbitMQ，避免与 DB password 冲突）
    if [ -n "$rabbitmq_password" ]; then
        content=$(echo "$content" | sed "s|password: $rabbitmq_password|password: YOUR_RABBITMQ_PASSWORD|g")
    fi
    if [ -n "$crawler_rabbitmq_password" ]; then
        content=$(echo "$content" | sed "s|password: $crawler_rabbitmq_password|password: YOUR_CRAWLER_RABBITMQ_PASSWORD|g")
    fi

    # 数据库 - 使用上下文感知替换（避免 username 和 password 相同时被错误替换）
    # 替换 "username: VALUE" 格式
    if [ -n "$db_username" ]; then
        content=$(echo "$content" | sed "s|username: $db_username|username: YOUR_DB_USERNAME|g")
    fi
    # 替换 "password: VALUE" 格式 - 注意：如果 password 值与 RabbitMQ 相同，这里不会再被替换
    if [ -n "$db_password" ]; then
        content=$(echo "$content" | sed "s|password: $db_password|password: YOUR_DB_PASSWORD|g")
    fi
fi

# 输出到标准输出
echo "$content"
