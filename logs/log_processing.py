import re

file_path = 'Final3.txt'  # Replace with the actual file path

# Initialize variables
ts_sum = 0
tj_sum = 0
count = 0

# Open the file and read its contents
with open(file_path, 'r') as file:
    for line in file:
        if line.startswith('ts_duration:'):
            ts_duration = line.split(':')[1].strip().split()[0]
            ts_sum += int(ts_duration)
            count += 1
        elif line.startswith('tj_duration:'):
            tj_duration = line.split(':')[1].strip().split()[0]
            tj_sum += int(tj_duration)

if count != 0:
    ts_avg = ts_sum / count
    tj_avg = tj_sum / count
    ts_avg_ms = ts_avg / 1e6
    tj_avg_ms = tj_avg / 1e6
    print(f'Average ts_duration: {ts_avg_ms} milliseconds')
    print(f'Average tj_duration: {tj_avg_ms} milliseconds')
else:
    print('No lines matching the pattern were found.')
