import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-language-switcher',
  imports: [CommonModule, TranslateModule],
  templateUrl: './language-switcher.component.html',
  styleUrl: './language-switcher.component.css',
})
export class LanguageSwitcherComponent {
  languages = [
    { code: 'pl', name: 'Polish', flag: 'PL' },
    { code: 'en', name: 'English', flag: 'GB' },
  ];

  currentLanguage = 'en';

  constructor(public translateService: TranslateService) {
    const savedLanguage = localStorage.getItem('language');
    if (savedLanguage) {
      this.currentLanguage = savedLanguage;
    }
  }

  switchLanguage(languageCode: string): void {
    this.currentLanguage = languageCode;
    this.translateService.use(languageCode);
    localStorage.setItem('language', languageCode);
  }

  getFlag(languageCode: string): string {
    const language = this.languages.find((lang) => lang.code === languageCode);
    return language ? language.flag : '';
  }
}
