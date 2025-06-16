import { Component } from '@angular/core';
import { StatisticsComponent } from '../statistics-status/statistics-status.component';
import { HairofferChartComponent } from '../hairoffer-chart/hairoffer-chart.component';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-statistic-dashboard',
  imports: [
    CommonModule,
    TranslateModule,
    StatisticsComponent,
    HairofferChartComponent,
  ],
  templateUrl: './statistic-dashboard.component.html',
  styleUrl: './statistic-dashboard.component.css',
})
export class StatisticDashboardComponent {}
