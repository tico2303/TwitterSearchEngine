# -* coding: utf-8 -*-
from tweepy import Stream
from tweepy.streaming import StreamListener
from authentication import authenticate
import json
import argparse
import os

class Listener(StreamListener):
    def __init__(self, outdir=None):
        self.outdir = outdir

    def on_data(self, data):
        try:
            if self.outdir == None:
                data_path = os.path.abspath(os.path.join(os.path.dirname( __file__ ), '..', 'data'))
            else:
                data_path = self.outdir
            if data_path[-1] != "/":
                data_path +="/"

            filename = data_path +'data.json'
            print "opening file: ",filename
            with open(filename, 'a') as f:
                # Check if lang = 'en'
                tweet = json.loads(data)
                if tweet['lang'] == "en":
                    print(data)
                    print("\n\n\n\n")
                    f.write(data)
                return True

        except BaseException as e:
            print("Error at: %s" % str(e))
        return True

    def on_error(self, status):
        print(status)
        return True

def get_arguments():
    parser = argparse.ArgumentParser(description='Twitter Scraper')
    parser.add_argument("--outdir", type=str, nargs="*",help="Labels or hashtags used to filter tweets")
    args = parser.parse_args()
    # print "outdir: ", args.outdir
    if args.outdir:
        return args.outdir[0]
    return None

if __name__ == "__main__":
    outdir = get_arguments()
    print("Authenticating...")
    twitter_stream = Stream(authenticate(), Listener(outdir))
    print("Scrapping Twitter...")
    # twitter_stream.filter(track=get_arguments())
    twitter_stream.sample()

