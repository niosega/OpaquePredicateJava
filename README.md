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

# Java bytecode limitation
<!-- TODO: expliquer pourquoi j'ai pas obfusqué les return et pourquoi j'ai utilisé que avec ces 3 visiteurs -->

# Garbage generation
If the predicate is well choosen but the garbage generation to obvious, the reverse engineer may spot something strange and find quickly that the branching is useless. In my implementation, for now I only generate a `iconst_3` instruction in the always-false branch. But this is obviously bad.

Collberg, Thomborson and Low have written that the more a code has branching, the more it looks "real". It may also be a good idea to include standard library calls to look more "real". This part is in fact way more difficult than chossing a good opaque predicate, because it relies on human perception of code, and two differents persons may have two different perceptions.

[1] https://www.researchgate.net/publication/37987523_A_Taxonomy_of_Obfuscating_Transformations