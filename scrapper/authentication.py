import tweepy
from tweepy import OAuthHandler
import os

def authenticate():
    consumer_key = 'jIxauKBrCGBU8TkXZHq7KW4iv'
    consumer_secret = 'KzK5QRAhK2GizokOaauEeFiGprhtqudAxdhVPbJPzlZDDBqbtr'
    access_token = os.environ.get('TWITTER_ACCESS_TOKEN')
    access_secret = os.environ.get('TWITTER_ACCESS_SECRET')

    auth = OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_token, access_secret)
    return auth
