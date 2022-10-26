# TP2 : Sed, the stream editor
## WANG Chen APP2

***
Interface, named type (class, record) et unnamed type (lambda)
Le but de ce TP est d'implanter une petite partie des commandes de l'outil sed.
***
* **Exercice 1 - Maven :**


  Créer un projet Maven en cochant create simple project au niveau du premier écran, puis passer à l'écran suivant en indiquant Next.
  Pour ce TP, le groupId est fr.uge.sed , l'artefactId est sed et la version est 0.0.1-SNAPSHOT. Pour finir, cliquer sur Finish.

***

* **Exercice 2 - Astra inclinant, sed non obligant :**

    
Le but de cet exercice est de créer un petit éditeur comme sed.
Pour ceux qui ne connaîtraient pas sed, c'est un utilitaire en ligne de commande qui prend en entrée un fichier et génère en sortie un nouveau fichier en effectuant des transformations ligne à ligne. sed permet facilement de supprimer une ligne soit spécifiée par son numéro, soit si elle contient une expression régulière ou de remplacer un mot (en fait une regex) par un mot.
L'utilitaire sed traite le fichier ligne à ligne, il ne stocke pas tout le fichier en mémoire (ce n'était pas une solution viable à la création de sed en 1974). On parle de traitement en flux, en stream en Anglais, d'où le nom de Stream EDitor, sed.


Les tests JUnit 5 de cet exercice sont StreamEditorTest.java. 

* **Q1**

Réponse :

On va créer une classe StreamEditor dans le package fr.uge.sed avec une méthode d'instance transform qui prend en paramètre un LineNumberReader et un Writer et écrit, ligne à ligne, le contenu du LineNumberReader dans le Writer
BufferedReader possède une méthode readLine() et un Writer une méthode append().
```java
public class StreamEditor(){
  public StreamEditor(){
    //constructeur
  }

  public StreamEditor(Command command){
    this.command = command;
  }
  public void transform(LineNumberReader numberReader, Writer w) throws IOException {
    Objects.requireNonNull(numberReader);
    Objects.requireNonNull(w);
    String e; //stocker les lignes
    while ((e = numberReader.readLine()) != null) {
      if (numberReader.getLineNumber()!=command.line()){
        w.append(e);
        w.append("\n");
      }
    }
  }
}
```

* **Q2**

Réponse :

on parcourt le fichier ligne à ligne comme précédemment, mais si le numéro de la ligne courante est égal à celui de la ligne à supprimer, alors on ne l'écrit pas dans le Writer.

```java
//On a besoin un record pour qui indique quelle ligne est supprimée
public record LineDeleteCommand(int line)  {
  //type primitif donc on a besoin de IAE
  public LineDeleteCommand{
    if (line < 0){
      throw new IllegalArgumentException("line must be positive");
    }
  }
}

//Après on ajoute une tableau de LineDeleteCommand dans la classe StreamEditor
  private final ArrayList<LineDeleteCommand> lineDeleteCommands = new ArrayList<>();

//Et la méthode lineDelete(int i) dans la classe
public static LineDeleteCommand lineDelete(int i){
  return new LineDeleteCommand(i);
};

```

* **Q3**

On souhaite maintenant écrire un main qui prend en paramètre sur la ligne de commande un nom de fichier, supprime la ligne 2 de ce fichier et écrit le résultat sur la sortie standard.


Réponse :

```java
public static void main(String[] args) {
    var nomFichier = args[0];
    var deleteLine = StreamEditor.lineDelete(2);

    try(var fichier = Files.newBufferedReader(Path.of(nomFichier));
        Writer out = new BufferedWriter(new OutputStreamWriter(System.out))) {
        //write(output))
      var output = new StreamEditor(deleteLine); //new class StreamEditor (methodes+champs)


      var line = new LineNumberReader(fichier); //read number
      output.transform(line, out);
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }
```

* **Q4**

Réponse :

On ajoute l'enum Action dans la classe StreamEditor.
```java
public enum Action {
    DELETE, PRINT
  }
```

* **Q5**

Réponse :

La question cinq nous demande de créer un l'interface pour les deux récord (FindAndDeleteCommand et LineDeleteCommand). 
Donc j'ai créé un Record FindAndDeleteCommand et puis une interface.
```java
public record FindAndDeleteCommand(Pattern p)  {
  public FindAndDeleteCommand {
    Objects.requireNonNull(p);
  }
}

public static FindAndDeleteCommand findAndDelete(Pattern p) {
  return new FindAndDeleteCommand(p);
}
```

Après j'ai modifié la méthode transform pour qu'elle puisse fonctionner avec l'interface.


* **Q6**

En fait, cette implantation n'est pas satisfaisante, car les records LineDeleteCommand et FindAndDeleteCommand ont beaucoup de code qui ne sert à rien. Il serait plus simple de les transformer en lambdas, car la véritable information intéressante est comment effectuer la transformation d'une ligne.
Modifier votre code pour que les implantations des commandes renvoyées par les méthodes lineDelete et findAndDelete soit des lambdas.

Réponse :

On dois maintenant transformer les Record en l'interface fonctionnelle (lambda).
```java
//Dans la classe StreamEditor :
private final Command command;

@FunctionalInterface
public interface Command {
    Action selectCommand(String s, Integer i); //
}

public void transform(LineNumberReader numberReader, Writer w) throws IOException {
  Objects.requireNonNull(numberReader);
  Objects.requireNonNull(w);
  String e; //stocker les lignes
  while ((e=numberReader.readLine())!=null){
    if (numberReader.getLineNumber()!=command.line()){
      w.append(e);
      w.append("\n");
    }
    if (!findAndDeleteCommand.p().matcher(e).find()){
          //trouver si la commande on veut delete est égale à la command dans e
          w.append(printAction.text());//Parce que ici c<est plus ''
          w.append("\n");
    }
  }
}
```

* **Q7 & Q8**

On souhaite maintenant introduire une commande substitute(pattern, replacement) qui dans une ligne remplace toutes les occurrences du motif par une chaîne de caractère de remplacement. Malheureusement, notre enum Action n'est pas à même de gérer ce cas, car il faut que la commande puisse renvoyer PRINT mais avec une nouvelle ligne.
On se propose pour cela de remplacer l'enum Action par une interface et DELETE et PRINT par des records implantant cette interface comme ceci :
On peut enfin ajouter la commande substitute(pattern, replacement) telle que le code suivant fonctionne.

```java
private interface Action {
         record DeleteAction() implements Action {}
         record PrintAction(String text) implements Action {}
}

// Q8
var command = StreamEditor.substitute(Pattern.compile("foo|bar"), "hello");
var editor = new StreamEditor(command);
editor.transform(reader, writer);
```
Réponse :

```java
public sealed interface Action {
  // sealed parce que on a utiliser le switch et case Action.xxx
  record DeleteAction() implements Action {}
  record PrintAction(String text) implements Action {}
}
  public StreamEditor(){
    this.command = (String s, Integer i) -> {
      return new Action.PrintAction(s); //Au début on n'a rien à delete
    };
  }

  public StreamEditor(Command command){
    this.command = command;
  }

  /*Q6*/
  @FunctionalInterface
  public interface Command {
    Action selectCommand(String s, Integer i);
  }

  public void transform(LineNumberReader numberReader, Writer w) throws IOException {
    Objects.requireNonNull(numberReader);
    Objects.requireNonNull(w);
    String e; //stocker les lignes
    while ((e=numberReader.readLine())!=null){
      
      switch (command.selectCommand(e, numberReader.getLineNumber())) {
        case Action.DeleteAction deleteAction -> { }
        case Action.PrintAction printAction -> {
        }
      }
    }
  }

  public static Command lineDelete(int i){
    if (i<0){
      throw new IllegalArgumentException();
    }
    return (String s, Integer n) -> {
      if (n == i)
        return new Action.DeleteAction();
      return new Action.PrintAction(s);
    };
  }
  public static Command findAndDelete(Pattern p){
    Objects.requireNonNull(p);
    return (String s, Integer i) -> {
      if(p.matcher(s).find()){
        return new Action.DeleteAction();
      }
      return new Action.PrintAction(s);
    };
  }

  /*Q8*/
  public static Command substitute(Pattern pattern, String replacement) {
    Objects.requireNonNull(pattern);
    Objects.requireNonNull(replacement);
    return (String s, Integer i) -> {
      return new Action.PrintAction(pattern.matcher(s).replaceAll(replacement));
      //matcher nous aide trouver si s(line actuel) est égale à pattern
    };
  }
```
Et on n'a plus besoin l'interface Command, donc on peut le mettre en commantaire.


* **Q10**

Enfin, on peut vouloir combiner plusieurs commandes en ajoutant une méthode andThen à Command tel que le code suivant fonctionne.
```java
var command1 = StreamEditor.substitute(Pattern.compile("foo"), "hello");
var command2 = StreamEditor.findAndDelete(Pattern.compile("baz"));
var editor = new StreamEditor(command1.andThen(command2));
editor.transform(reader), writer);

```
andThen doit appliquer la première commande puis la seconde (dans cet ordre).
Modifier Command pour introduire la méthode d'instance andThen.


* Réponse :


* **11**

En conclusion, dans quel cas, à votre avis, va-t-on utiliser des records pour implanter de différentes façons une interface et dans quel cas va-t-on utiliser des lambdas ?

* Réponse :

Typage == une interface
Pour abstraire plusieurs comportements possibles, on utilise une interface.

Lambda == instance d’une classe qui implante l’interface
Lambda est une instance d’une classe qui implante l’interface
La classe est générée à l’exécution ou le typage vient de l’interface
***
