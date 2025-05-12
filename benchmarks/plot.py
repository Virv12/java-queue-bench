#!/usr/bin/env python3

from dataclasses import dataclass
from pathlib import Path
import matplotlib.pyplot as plt


@dataclass
class Data:
    bench: str
    queue: str
    time: list[float]

    @staticmethod
    def parse(s: str):
        a, b, *other = s.split()
        return Data(a, b, list(map(float, other)))


def main(file: Path):
    data = [Data.parse(line) for line in file.read_text().splitlines()]

    parts = {}
    for d in data:
        l = parts.setdefault(d.bench, [])
        l.append(d)

    for b, data in parts.items():
        plt.figure()

        names = [d.queue for d in data]
        values = [d.time[2] for d in data]
        errors = [
            [d.time[2] - d.time[1] for d in data],
            [d.time[3] - d.time[2] for d in data],
        ]

        plt.bar(names, values, yerr=errors, capsize=5, ecolor='black', linewidth=1)

        plt.title(b)

        plt.xticks(rotation=90)

        plt.ylabel('Time [ns]')
        plt.yscale('log')

        plt.tight_layout()
        plt.savefig(f'{b}.svg')
        plt.close()

        print(f"Saved {b}.svg")


if __name__ == "__main__":
    main(Path(__file__).parent / 'bench.txt')
