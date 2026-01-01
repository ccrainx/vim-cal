# Vim-Style Java Calculator

A terminal calculator with Vim-style navigation (`h/j/k/l`) supporting basic arithmetic (+, -, *, /).

---

## Quick Start

### Run from `.jar`

```bash
javac main.java        # compile if needed
java -jar main.jar     # run
```
---
# Run with Docker
```
docker build -t calc-app .
docker run -it calc-app
```
## Example Dockerfile:
```
FROM openjdk:21-slim
WORKDIR /app
COPY main.jar .
CMD ["java", "-jar", "main.jar"]
```
---
# Run with Nix Flakes
```
# Build with flakes
nix --extra-experimental-features nix-command --extra-experimental-features flakes build .#calc-app

# Run the calculator
./result/bin/calc
```

# Controls

``` h ``` = move left

``` l ``` = move right

``` k ``` = move up

``` j``` = move down

``` Enter ``` = select number/operator

``` x ``` = delete last input

``` q ``` = quit

**Errors** (invalid input or divide by zero) are highlighted in **red.**
