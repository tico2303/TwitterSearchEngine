from wtforms import Form, StringField, SelectField, SubmitField

class TwitterSearchForm(Form):
    """
    indexFields = [("text", "text"),
                   ("hashtags", "hashtags"),
                    ("timestamp","timestamp"),
                    ("location","location"),
                    ("username","username")
                    ]
    """
    indexFields = [("text", "text"),
                    ("hashtags", "hashtags"),
                    ("username","username")
                    ]
    select = SelectField("Filter by: ",choices=indexFields)
    search = StringField('Query for: ')
    submit = SubmitField("Search")
