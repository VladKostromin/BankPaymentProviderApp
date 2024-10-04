# Используем официальный образ OpenJDK 21
FROM openjdk:21-jdk-slim

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файл сборки проекта (JAR) в контейнер
COPY target/src.jar /app/bank-provider-app.jar

# Устанавливаем переменные среды для настройки контейнера
ENV JAVA_OPTS=""

# Запуск приложения
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/your-app.jar"]

# Указываем порт, который будет открыт в контейнере
EXPOSE 8091