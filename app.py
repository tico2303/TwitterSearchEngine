from __future__ import print_function
from flask import Flask
from flask import render_template, request, flash, redirect, url_for
from forms import TwitterSearchForm
from subprocess import Popen, PIPE, STDOUT
# import Searcher
import json
import threading

from flask_googlemaps import GoogleMaps
from flask_googlemaps import Map
import subprocess
import geocoder
import pprint
import threading
from datetime import datetime
import re
import requests
import os

app = Flask(__name__)
GoogleMaps(app)

location_pattern = r'(\w{3,}),[\s]*([a-zA-Z]{2})(?![a-zA-Z0-9])'
regex = re.compile(location_pattern)
ranked_data = []

#Used for visualizing Tweet Json structure
pp = pprint.PrettyPrinter(indent=4)
def pprint(data):
    pp.pprint(data)

# creates Java Searcher Binary
def createBinaries():
    if not os.path.isfile("./TwitterSearchEngine/Searcher.class"):
        cmd = ["javac", "Searcher.java"]
        p = subprocess.Popen(cmd)

# checks if image for user exists
def exists(url):
    try:
        r = requests.head(url)
        return r.status_code == requests.codes.ok
    except:
        return False

# adding to exists function to jinja environment
app.jinja_env.globals.update(exists=exists)

# gets location data for use in Flask-Google-Maps Map class
def getLocations(results,markers):
    for tweet in results:
        if tweet['coords'] != None:
            info = {}
            info['icon'] = 'http://maps.google.com/mapfiles/ms/icons/green-dot.png'
            info['lat'] = tweet['coords'][0]
            info['lng'] = tweet['coords'][1]
            info['infobox'] = "<img src='"+tweet["userimageurl"]+"'/>"
            markers.append(info)
        elif tweet['location'] != None:
            match = regex.match(tweet['location'])
            if match:
                try:
                    loc = geocoder.location(tweet['location'])
                    info = {}
                    info['icon'] = 'http://maps.google.com/mapfiles/ms/icons/green-dot.png'
                    info['lat'] = loc.latlng[0]
                    info['lng'] = loc.latlng[1]
                    info['infobox'] = "<img src='"+tweet["userimageurl"]+"'/><br>"+tweet["username"]
                    markers.append(info)
                except:
                    print("no coorinates for location: ", tweet['location'], " could be found")
# partitions list into n elements
def partitionList(li, num_partitions):
    return [li[i::num_partitions] for i in xrange(num_partitions)]

@app.route('/api/results',methods=['POST'])
def results():
    if request.method == "POST":
        if request.headers["Content-Type"] == 'application/json':
            json_data = request.json
            # sort by rank
            ranked_data = sorted(json_data, key=lambda x:x["rank"])
            for i,l in enumerate(ranked_data):
                if i == 10:
                    break
            return "flask server::JSON recieved is: "+json.dumps(request.json)
    return "GOT NOTHING GOOD (aka. NO JSON)"

@app.route('/',methods=['GET','POST'])
def index():
    search = TwitterSearchForm(request.form)
    if request.method == "POST":
       return search_results(search)
    return render_template( 'index.html', form=search )

@app.route('/results')
def search_results(search):
    q = search.data["search"]
    index = search.data["select"]
    markers = []
    print("[+] Querying Searcher..")
    try:
        cmd = ["java", "Searcher", "-q", q, "-f", index]
        p = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = p.communicate()
        res = json.loads(out)
    except:
        res={'results':[]}
        print("[!] NO Results: ", res)
    print("[+] Done Querying Searcher")
    if len(res['results']) != 0:
        # TODO: Move time parsing to backend Searcher class
        print("[+] Parsing Time: ")
        for i, t in enumerate(res['results']):
            tstamp = t["timestamp"].split(" ")
            dow = tstamp[0]
            month = tstamp[1]
            day = tstamp[2]
            time = tstamp[3]
            year = tstamp[5]
            d = datetime.strptime(time, "%H:%M:%S")
            res['results'][i]["formatedtime"] = month +" "+ day+ " "+ year + " at: "+ d.strftime("%I:%M %p")
        print("[+] Done Parsing Time")
        print("[+] Creating Locations...")
        # partitionList into n parts to thread getlocation n times
        splitres =partitionList(res['results'], 8)
        threadList = []
        marks = []
        for result in splitres:
            t1 = threading.Thread(target=getLocations, args=(result,marks))
            t1.start()
            threadList.append(t1)
        for thread in threadList:
            thread.join()
        markers += marks

    if len(markers) == 0:
            info = {}
            info['icon'] = 'http://maps.google.com/mapfiles/ms/icons/green-dot.png'
            info['lng'] = -117.3755
            info['lat'] = 33.9806
            info['infobox'] = "Testing"
            markers.append(info)
    cur_location = geocoder.ip('me').latlng
    tweetmap = Map(identifier="tweetmap",
                    zoom=9,
                    fit_markers_to_bounds=True,
                    lat=cur_location[0],
                    lng=cur_location[1],
                    markers=markers) 
    print("[+] Done Creating Locations")
    print("[+] Done processing Results... Rendering 'results.html'")
    return render_template( 'results.html', ranked_data=res["results"], tweetmap=tweetmap)

if __name__ == "__main__":
    # set host='0.0.0.0' to host on lan using your computers ip
    #app.run(host='0.0.0.0')
    createBinaries()
    app.run()

