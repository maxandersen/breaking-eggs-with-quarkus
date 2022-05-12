ps -o rss -o command | grep $1 | awk '{print $1}' | head -n 100 | awk '{sum+=$1/1024}END{print sum}'
