# Opaque Predicate in Java

# Principle
Opaque predicate technique consists of introducing branches in the code whose predicate always yields the same result. On the always executed path, the original code execution continues as usual, on the never executed path, some invalid execution takes place. However, this predicate should be opaque, such that a reverse engineer cannot identify it and remove it.

# Predicate choice
The predicate choice is the key of this obfuscation. A too simple predicate will be easily removed by the reverse engineer and a too computational expensive will have a huge impact on program performance.

<!-- TODO: For the sake of simplicity, expliquer ici mon choix de prédicat -->

Collberg, Thomborson and Low put opaque predicate in 3 categories : trivial, weak and strong[1].

> An opaque construct is trivial if a deobfuscator can crack it (deduce its value) by a static local analysis. An analysis is local if it is restricted to a single basic block of a control flow graph.
> -- <cite>C. Collberg, C. Thomborson, D. Low</cite>

> An opaque construct is weak if a deobfuscator can crack it by a static global analysis. An analysis is global if it is restricted to a single control flow graph.
> -- <cite>C. Collberg, C. Thomborson, D. Low</cite>

A strong opaque predicate is a predicate that is not trivial or weak. The authors mention that, even if it may involve multi-CFG analysis, using predicate involving standard library call is useless because the reverser knows the behavior of the standard library. For example :

```
int a = random(1, 10)
if (a * a > 0) {...}
```
The predicate `a * a > 0` is not always true because `a * a` may be equals to zero. So the reverser need to analyse the potential value of `a`  which comes from the function call `random(1, 10)`. The analysis of this call theoritically involves complex reverse engineering task. But in fact no, everyone knows that `random(1, 10)` will returns something between 1 and 10, and thus `a * a > 0` is always true.

A more advanced technique would be to use custom functions that do the same thing as a standard library function. An adaptation of your previous example may be for instance :
```
int a = veryComplexFunctionWithUselessParams("fish", 10, new FunObject(), 1);
if (a * a > 0) {...}
```

For the sake of simplicity, I decided to implement the very basic `a * a < 0` predicate, `a` being chosen in the local variable of the method. If no suitable local variable are found, the obfuscator bails out for this method. It is of course a very bad opaque predicate. Such very simple predicate can easily be removed using either:

- the brain of the reverser.
- a constraint solver, like z3 and very basic CFG analysis.

In real life application, I would have chosen a strong opaque predicate, but it would have been require a lot more time (in term of thinking and implementation).

# Garbage generation
If the predicate is well choosen but the garbage generation to obvious, the reverse engineer may spot something strange and find quickly that the branching is useless.

Collberg, Thomborson and Low have written that the more a code has branching, the more it looks "real". It may also be a good idea to include standard library calls to look more "real". This part is in fact way more difficult than chossing a good opaque predicate, because it relies on human perception of code, and two differents persons may have two different perceptions.

# Implementation considerations

## Java version
This implementation has been tested with openjdk-11.

## How it works ?
The .class to obfuscate is first read by a `ClassReader`. The `ClassReader` gives a `ClassNode`, which is an AST-representation of the class. I iterate over the `MethodNode` of the `ClassNode`. For each `MethodNode`, I iterate over the instructions, leaving untouched some instructions :

- the ones my tool is not yet able to handle.
- the `*return` instruction, because it would require specific handling so that the method always returns something.

After the transformation, the modified `ClassNode` is given to a `ClassWriter` and then the modified .class is written on disk.

You can have an high-level view of how the code works by reading `OpaquePredicateObfuscator.java:obfuscate`. The details of the implementation are in the file `OpaquePredicateTransformer.java`.

## How to run it ?
At the root of the project, launch :
```
./gradlew clean build test
```

It should print :

```
nico@machine: ~/OpaquePredicateJava (main) $ ./gradlew clean build test
Starting a Gradle Daemon, 2 incompatible Daemons could not be reused, use --status for details

> Task :app:test

Test > TestSimpleCondition PASSED

Test > TestSumLoop PASSED

Test > TestMonteCarlo SKIPPED

Test > testRecursive PASSED

BUILD SUCCESSFUL in 6s
8 actionable tasks: 8 executed
```

## Source debug informations
To be able to use local variable information, the original source code must be compiled with the `javac -g` option.

## Garbage generation
The obfuscator generates garbage code for the never-true branch. For now, it only generate instruction that pop/push the same amount of thing on the stack as the opcode being surronded with branches. As mentionned before, the garbage generation is very difficult because it relies on human perception of "garbage".

## Launch option
The generated garbage does not take into account the code that comes after the branching. So it can create inconsistency that make the verifier fails. For example, my garbage may push int on the stack, but if the next instruction expects a double, the verifier fails. Here is an example :

```
if x*x >= 0
    dconst_0
else
   //garbage: Details at OpaquePredicateTransformer.java:generateGarbage
   iconst_5 

dmul
```

`dmul` expects a `double` on the stack, but the `else` branch, even if we will never execute it, push an `int`. So the verifier fails.

So for the sake of simplicity, I disable the verifier (with `java -noverify` or `gradle` options) when I launch the obfuscated class.

# Tests
For now, I only test the obfuscation with basic Java programs that I write myself. A better solution would be to choose a open source project of a correct size and compare the result of the original program and of the obfuscated program.

The MonteCarlo test is for now ignored because it crashes the JVM. There is still work to be done :)

[1] https://www.researchgate.net/publication/37987523_A_Taxonomy_of_Obfuscating_Transformations