read -p "Mensagem do commit: " -r text

git pull origin master

git add .

git commit -m "$text"

git push origin master

Echo "Deu tudo certo"
