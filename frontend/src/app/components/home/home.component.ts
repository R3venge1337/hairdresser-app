import { CommonModule } from '@angular/common';
import {
  Component,
  effect,
  inject,
  Injector,
  signal,
  WritableSignal,
} from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';

interface RawCard {
  titleKey: string;
  descriptionKey: string;
  imageUrl: string;
}

interface TranslatedCard {
  title: string;
  description: string;
  imageUrl: string;
}

@Component({
  selector: 'app-home',
  imports: [CommonModule, TranslateModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  constructor() {}
}
