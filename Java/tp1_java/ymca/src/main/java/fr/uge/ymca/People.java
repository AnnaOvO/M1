package fr.uge.ymca;

public sealed interface People permits VillagePeople, Minion{ //Q7 sealed
  String name();
  //public int price(); Mettre en commentaire Q6
  
  
}
