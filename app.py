from flask import Flask
from flask import render_template, request, flash, redirect
from forms import TwitterSearchForm
from subprocess import Popen, PIPE, STDOUT
import Searcher

app = Flask(__name__)

def getSearchResults(query, field):
    s = Searcher()
    s.search(query,field)
    results = []
    q = query
    print("q: ",q)
    indexField = field
    print("indexField: ",indexField)
    """
    javaproc = Popen(['java','Searcher','-q', q ,'-f', indexField],stdout=PIPE,stderr=STDOUT)
    for line in javaproc.stdout:
        results.append(line)
        #print(line)
    print(",".join(results))
    """

@app.route('/',methods=['GET','POST'])
def index():
    search = TwitterSearchForm(request.form)
    if request.method == "POST":
       return search_results(search)
    return render_template( 'index.html', form=search )

@app.route('/results')
def search_results(search):
    query_string = search.data['search']
    print("query_string: ", query_string)
    indexField = search.data['select']
    print("indexField: ", indexField)
    getSearchResults(query_string, indexField) 
    return render_template("results.html")

if __name__ == "__main__":
    app.run()
