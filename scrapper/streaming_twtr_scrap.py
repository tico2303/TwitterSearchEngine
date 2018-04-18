# -*- coding: utf-8 -*-
from tweepy import Stream
from tweepy.streaming import StreamListener
from authentication import authenticate
import json
import argparse

class Listener(StreamListener):

    def on_data(self, data):
        try:
            with open('ai_data.json', 'a') as f:
                # data = json.dumps(data, ensure_ascii=False).encode("utf8")
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
    parser.add_argument("--filter_by", type=str, nargs="*",help="Labels or hashtags used to filter tweets")
    args = parser.parse_args()
    print args.filter_by
    return args.filter_by

if __name__ == "__main__":
    get_arguments()
    twitter_stream = Stream(authenticate(), Listener())
    print("calling twitter_stream.filter()")
    twitter_stream.filter(track=get_arguments())

