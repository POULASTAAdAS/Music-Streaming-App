# select non punjabi songs published after 2022
import json
import os
import shutil
import subprocess

count = 0

copyFromDir = "F:/processed/"
copyToDir = "E:/22-24/"

listOfSongs = os.listdir(copyToDir)

for song in listOfSongs:
    fileFromRootPath = os.path.join(copyToDir, song)
    try:
        command = [
            'ffprobe',
            '-v', 'quiet',
            '-print_format', 'json',
            '-show_format',
            '-show_streams',
            fileFromRootPath
        ]

        result = subprocess.run(command, capture_output=True, text=True)

        if result.returncode == 0:
            metadata = json.loads(result.stdout)
            tags = metadata.get('format', {}).get('tags', {})

            if "Punjabi" in tags["album"]:
                os.remove(fileFromRootPath)
                print(fileFromRootPath)

        #     if not any(genre.lower() in {"punjabi", "bhojpuri"} for genre in tags["genre"]):
        #         if int(tags["date"]) > 2021:
        #             count += 1
        #             copyToSongRootPath = os.path.join(copyToDir, song)
        #             shutil.copy(fileFromRootPath, copyToSongRootPath)
        #
        #             print("copied: ", song, "count: ", count)

    except Exception as e:
        continue
