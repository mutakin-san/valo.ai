system guide:
1. Clone repository -> git clone https://github.com/stheven26/capstone-api-ml
2. Install library -> pip install -r requirements.txt 
3. Run Program -> env\Scripts\acrivate -> python main.py 
4. Testing API in Postman
    http://127.0.0.1:5000//recommendations/vaccine
    - iinput vaccine data with one of the types of vaccine injected to you ["AZ", "Sinovac","Sinopharm", "Pfizer", "Moderna", "Janssen"]
5. Deploy to gcp with Google Cloud SDK Shell
- gcloud auth login -> login with email
- gcloud set config project [project id]
- gcloud app deploy