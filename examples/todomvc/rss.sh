while true
do
	htop -p `ps aux | grep 'java\|runner' | grep -v eclipse | grep -v vscode | grep -v vscodium | awk '{print $2}' | paste -d',' -s -`
done

