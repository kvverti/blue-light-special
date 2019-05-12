import sys

colors = [
    'white',
    'orange',
    'magenta',
    'light_blue',
    'yellow',
    'lime',
    'pink',
    'gray',
    'light_gray',
    'cyan',
    'purple',
    'blue',
    'brown',
    'green',
    'red',
    'black'
]

if __name__ == '__main__':
    if len(sys.argv) != 4:
        print('Usage:', sys.argv[0], '<path> <base file> <src color>')
        exit(0)
    path = sys.argv[1]
    base_file = sys.argv[2]
    src_color = sys.argv[3]
    data = ''
    with open(f'src/main/resources/{path}/{src_color}_{base_file}', encoding='UTF-8') as src_file:
        data = src_file.read()
    for color in colors:
        if color != src_color:
            new_data = data.replace(src_color, color)
            with open(f'src/main/resources/{path}/{color}_{base_file}', 'w', encoding='UTF-8') as dst_file:
                dst_file.write(new_data)
