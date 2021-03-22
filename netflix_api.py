import http.client
import json
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

conn = http.client.HTTPSConnection("unogsng.p.rapidapi.com")

headers = {
    'x-rapidapi-key': "***********************************",
    'x-rapidapi-host': "unogsng.p.rapidapi.com"
    }

conn.request("GET", "/search?type=movie&orderby=rating&audiosubtitle_andor=and&limit=100&subtitle=english&audio=english&offset=100&end_year=2021", headers=headers)

data = conn.getresponse().read().decode("utf-8")
json_data = json.loads(data)

movies_data=json_data["results"]

if not firebase_admin._apps:
    cred = credentials.Certificate(r'C:\Users\Vikram\Happy-Android-Backend\happy-*****-firebase-adminsdk***************.json') 
    firebase_admin.initialize_app(cred)

db = firestore.client()

for movie in movies_data:
    db.collection("movies").add(movie)
