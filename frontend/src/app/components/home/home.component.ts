import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  cards = [
    {
      title: 'Stylizacja włosów',
      description:
        'Oferujemy profesjonalne usługi stylizacji włosów, które podkreślą Twoją urodę. Zaufaj naszym doświadczonym fryzjerom!',
      imageUrl: 'home1.jpg',
    },
    {
      title: 'Koloryzacja',
      description:
        'Zmień swój wygląd dzięki naszym usługom koloryzacji. Oferujemy szeroki wybór kolorów i technik, które spełnią Twoje oczekiwania.',
      imageUrl: 'home2.jpg',
    },
    {
      title: 'Pielęgnacja włosów',
      description:
        'Dbaj o swoje włosy z naszymi specjalistycznymi zabiegami pielęgnacyjnymi. Oferujemy produkty najwyższej jakości, które odżywią Twoje włosy.',
      imageUrl: 'home3.jpg',
    },
  ];
}
