# -*- coding: utf-8 -*-
import json
import pprint

pp = pprint.PrettyPrinter(indent=4)

def pprint(data):
    pp.pprint(data)

def process_tweet(filename):
    with open(filename,'r') as f:
         for raw_tweet in f.readlines():
            tweet = json.loads(raw_tweet.strip('\r\n'))
        # tweet = json.loads(f.readline().strip('\r\n'))
            pprint(tweet)
            print("*"*20, " Keys ", "*"*20)
            pprint(tweet.keys())

            get_text(tweet)
            if 'geo' in tweet.keys():
                if(tweet['geo'] != None or tweet['coordinates'] != None or tweet['place'] != None):
                    raw_input("\n\nEnter to see next tweet\n\n")

def get_text(tweet):
    print("*"*30)
    # print(tweet['text'])
    
    if'retweeted_status' in tweet.keys():
        if tweet['retweeted_status']['truncated'] == "True":
            print "Truncated....................."
        if 'extended_tweet' in tweet['retweeted_status'].keys():
            pprint(tweet['retweeted_status']['extended_tweet']['full_text'])
    # pprint(tweet['retweeted_status']['extended_tweet'].keys())
    print("*"*30)
    # pprint(tweet['text'].decode("unicode_escape"))



if __name__ == "__main__":
    process_tweet('ai_data.json')
