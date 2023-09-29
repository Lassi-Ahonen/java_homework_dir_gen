# Java Homework Directory Generator 
A command line tool for creating directory structures for our java programming homework.

I have included a template .java file alongside the program if you need it.

Feel free to create issues here on Github for anything that does not work and L'll try to fix it when i have time.

## Usage
### Compile
```
javac HomeworkDirectoryGen.java
```
### Run
```
java HomeworkDirectoryGen
```
### Inputs
Input output path [homework directory]: PATH_TO_YOUR_HOMEWORK_DIR
```
F:\4a00ek44-3016-introduction-to-programming-ahonen-lassi\homework
```
Input root directory name to generate [01, 02... etc]: NAME_OF_THE_ROOT_DIR
```
01
```
How many exercises to generate? [e01, e02... etc]: AMOUNT
```
15
```
Optional: Generates a template file for each exercise, you can leave this empty if none should be generated.
Input path of template file to generate [press enter to skip]: PATH_TO_TEMPLATE_JAVA_FILE
```
F:\4a00ek44-3016-introduction-to-programming-ahonen-lassi\homework\Main.java
```
Or you can input nothing and the program will not generate given file.

These inputs would generate directory structure of `homework\01\e01\Main.java` for e01-15.
