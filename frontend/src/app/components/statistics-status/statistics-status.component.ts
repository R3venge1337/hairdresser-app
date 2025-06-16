import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { TranslateModule } from '@ngx-translate/core';
import { ChartOptions, ChartConfiguration } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { StatisticService } from '../../services/statistic.service';
import { Statistics } from '../../models/statistics';

@Component({
  selector: 'app-statistics',
  imports: [CommonModule, BaseChartDirective, MatCardModule, TranslateModule],
  templateUrl: './statistics-status.component.html',
  styleUrl: './statistics-status.component.css',
})
export class StatisticsComponent implements OnInit {
  chartStatusTitle: string = '';
  constructor(private statisticService: StatisticService) {}

  public pieChartOptions: ChartOptions<'pie'> = {
    responsive: true,
    maintainAspectRatio: false, // Ważne dla elastycznego rozmiaru w kontenerze
    plugins: {
      legend: {
        display: true,
        position: 'top',
        labels: {
          color: '#333', // Kolor tekstu legendy
        },
      },
      datalabels: {
        formatter: (value, ctx) => {
          return `${value}`;
        },
        color: '#fff', // Kolor tekstu etykiety danych
        font: {
          weight: 'bold',
          size: 14,
        },
        display: 'auto', // Pokaż etykiety tylko, jeśli jest wystarczająco miejsca
      },
      title: {
        display: false,
        text: this.chartStatusTitle,
      },
    },
  };

  public pieChartLabels: string[] = [];
  public pieChartData: ChartConfiguration<'pie'>['data'] = {
    labels: this.pieChartLabels,
    datasets: [
      {
        data: [],
        backgroundColor: [
          // Przykładowe kolory, możesz dostosować
          '#42A5F5', // CONFIRMED (niebieski)
          '#FF6384', // CANCELED (czerwony)
          '#66BB6A', // COMPLETED (zielony)
          '#FFCE56', // RESCHEDULED (żółty)
          '#9C27B0', // CANCELED (fioletowy)
          '#607D8B', // Inne (szary)
        ],
        hoverBackgroundColor: [
          // Kolory po najechaniu myszą
          '#64B5F6',
          '#FF9AA2',
          '#81C784',
          '#FFE082',
          '#BA68C8',
          '#90A4AE',
        ],
        borderColor: '#fff', // Ramka wokół segmentów
        borderWidth: 2,
      },
    ],
  };

  public pieChartType: 'pie' = 'pie';

  ngOnInit(): void {
    this.loadAppointmentsByStatus();
  }

  async loadAppointmentsByStatus(): Promise<void> {
    try {
      // Oczekujemy tablicy obiektów Statistics
      const statisticsArray: Statistics[] =
        await this.statisticService.getAppointmentsByStatus();

      // Mapujemy tablicę obiektów na dwie oddzielne tablice: labels i values
      const labels = statisticsArray.map((item) => item.status);
      const values = statisticsArray.map((item) => item.count);

      if (values.length > 0) {
        this.pieChartLabels = labels;
        this.pieChartData.labels = labels;
        this.pieChartData.datasets[0].data = values;
      } else {
        console.log('No data received or processed for the chart.');
        this.pieChartData.datasets[0].data = [];
        this.pieChartLabels = [];
      }
    } catch (error) {
      console.error('Failed to load appointments by status for chart:', error);
      this.pieChartData.datasets[0].data = [];
      this.pieChartLabels = [];
    }
  }
}
