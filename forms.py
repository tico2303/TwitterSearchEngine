from wtforms import Form, StringField, SelectField

class TwitterSearchForm(Form):
    indexFields = [("text", "text"),
                   ("hashtags", "hashtags"),
                    ("timestamp","timestamp"),
                    ("location","location"),
                    ("username","username")
                    ]
    select = SelectField("Search for Tweet by: ",choices=indexFields)
    search = StringField('')
