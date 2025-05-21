import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, FooterComponent, TranslateModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'hairdresser-frontend';
  constructor(private translate: TranslateService) {
    // Set included languages
    this.translate.addLangs(['en', 'pl']);
    const browserLang = navigator.languages
      ? navigator.languages[0].split('-')[0]
      : navigator.language.split('-')[0];

    // Get the current browser language, if included set it
    const defaultLang = this.translate.getLangs().includes(browserLang)
      ? browserLang
      : 'en';

    // Set the default and current language
    this.translate.setDefaultLang(defaultLang);
  }

  switchLanguage(lang: string) {
    this.translate.use(lang);
  }
}
