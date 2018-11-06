from flask import Flask, jsonify
import audioTrainTest as aT

app = Flask(__name__)

audio = "training/audio1.wav"


@app.route('/detect', methods=['GET', 'POST'])
def detection(audio=audio):
    result = aT.fileRegression(audio, "training/", "svm")
    return jsonify(dict(zip(result[1], result[0])))


if __name__ == "__main__":
    result = aT.fileRegression(audio, "training/", "svm")
    print(result)
