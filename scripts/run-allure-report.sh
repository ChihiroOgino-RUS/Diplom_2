#!/usr/bin/env bash
# ==================================================
#  Запуск Allure-отчёта (Задание 2 — API-тесты)
#  Отчёт откроется в браузере из закоммиченных
#  результатов прогона — тесты запускать не нужно.
#
#  Чтобы обновить отчёт своим прогоном:
#    1) mvn clean test
#    2) скопируй содержимое target/allure-results
#       в scripts/allure-results
# ==================================================

set -e

# переходим в корень проекта (папка на уровень выше скрипта)
cd "$(dirname "$0")/.."

# проверяем, что Maven доступен
if ! command -v mvn >/dev/null 2>&1; then
    echo "Maven не найден в PATH. Установите Maven и попробуйте снова." >&2
    exit 1
fi

mvn allure:serve "-Dallure.results.directory=../scripts/allure-results"
