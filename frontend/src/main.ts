import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import axios from 'axios';
import { environment } from './environments/environment';
import { Chart, registerables } from 'chart.js';
import ChartDataLabels from 'chartjs-plugin-datalabels';

axios.defaults.baseURL = environment.apiUrl;

Chart.register(...registerables);
Chart.register(ChartDataLabels);

bootstrapApplication(AppComponent, appConfig).catch((err) =>
  console.error(err)
);
