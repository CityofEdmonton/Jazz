# Jazz - City of Edmonton Internal Application Backend Integration

## To run the project locally:
```
On your terminal:

# Create a virtual environment
1. virtualenv venv --python=python3

# Activate your virtual environment
2. source venv/bin/activate

# Install all dependencies
3. pip install -r requirements.txt

# Run database migrations
4. python3 manage.py migrate

# Create a local super user
5. python3 manage.py createsuperuser

# Run the server locally (by defult it's on http://127.0.0.1:8000/)
6. python3 manage.py runserver

# Create a docker image
7. docker build -t jazz_backend:latest .

# Tag the image
8. docker tag jazz_backend $Docker_repo/jazz-backend:latest

# Push to Docker repo
9. docker push $Docker_repo/jazz-backend:latest

# Run this on your k8s
10. kubectl run jazz-backend --image=$Docker_repo/jazz-backend:latest --port 8080

# Expose k8s
11. kubectl expose deployment jazz-backend --type=LoadBalancer --port 80 --target-port 8080
```
## Technologies

Here is a list of all the big technologies we use for integration:

- [**Django REST framework**](https://www.django-rest-framework.org/): REST API server

## Codebase overview

```
integrationJazz/
├── url.py           # All app level endpoints 
├── ...Handlers.py   # Handlers to each public endpoints
└── Helpers.py       # All functions used locally

mysite/
├── setting.py       # Settings to both integrationJazz app and the project 
└── url.py           # All project level endpoints
```