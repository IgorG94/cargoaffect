from flask import Flask, jsonify, request
from werkzeug.utils import secure_filename
import os
import audioTrainTest as aT

UPLOAD_FOLDER = './'

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = './'

audio = "training/audio1.wav"


@app.route('/detect', methods=['GET', 'POST'])
def detection():
    if request.method == 'POST':
        f = dict(request.files)[''][0]
        filename = secure_filename(f.filename)
        filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        f.save(filepath)
        result = aT.fileRegression(filepath, "training/", "svm")
        return jsonify(dict(zip(result[1], result[0])))


if __name__ == "__main__":
    result = aT.fileRegression(audio, "training/", "svm")
    print(result)
