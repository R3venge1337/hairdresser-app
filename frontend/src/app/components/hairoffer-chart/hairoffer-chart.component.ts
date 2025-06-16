import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { TranslateModule } from '@ngx-translate/core';
import { ChartOptions, ChartData } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { StatisticService } from '../../services/statistic.service';
import { HairOfferStatistics } from '../../models/statistics';

@Component({
  selector: 'app-hairoffer-chart',
  imports: [CommonModule, BaseChartDirective, MatCardModule, TranslateModule],
  templateUrl: './hairoffer-chart.component.html',
  styleUrl: './hairoffer-chart.component.css',
})
export class HairofferChartComponent implements OnInit {
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  constructor(private statisticService: StatisticService) {}

  public barChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'top',
        labels: {
          color: '#333',
        },
      },
      datalabels: {
        anchor: 'end',
        align: 'end',
        color: '#333',
        font: {
          weight: 'bold',
        },
        formatter: (value) => {
          return `${value}`;
        },
      },
      title: {
        display: true, // Zmieniono na true, żeby tytuł był widoczny
        text: '', // Ustawione dynamicznie w ngOnInit
      },
    },
    scales: {
      x: {
        grid: {
          display: false,
        },
        ticks: {
          color: '#333',
        },
      },
      y: {
        beginAtZero: true,
        ticks: {
          color: '#333',
        },
      },
    },
  };

  public barChartType: 'bar' = 'bar';

  public barChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: '', // Ustawione dynamicznie w ngOnInit
        backgroundColor: 'rgba(75, 192, 192, 0.6)',
        borderColor: 'rgba(75, 192, 192, 1)',
        borderWidth: 1,
      },
    ],
  };

  ngOnInit(): void {
    this.loadCompletedHairOfferCounts();
  }

  async loadCompletedHairOfferCounts(): Promise<void> {
    try {
      console.log('Attempting to load completed hair offer counts...'); // Dodano log
      const data: HairOfferStatistics[] =
        await this.statisticService.getCompletedHairOfferCounts();
      console.log('Received data from service:', data); // Dodano log

      const labels = data.map((item) => item.name);
      const counts = data.map((item) => item.count);
      console.log('Processed labels:', labels); // Dodano log
      console.log('Processed counts:', counts); // Dodano log

      if (counts.length > 0) {
        this.barChartData.labels = labels;
        this.barChartData.datasets[0].data = counts;
        console.log('Chart data updated:', this.barChartData); // Dodano log
        this.chart?.update(); // Odśwież wykres po załadowaniu danych
      } else {
        console.log(
          'No completed hair offer data to display. Setting empty data.'
        );
        this.barChartData.labels = [];
        this.barChartData.datasets[0].data = [];
        this.chart?.update();
      }
    } catch (error) {
      console.error('Failed to load completed hair offer counts:', error);
      this.barChartData.labels = [];
      this.barChartData.datasets[0].data = [];
      this.chart?.update();
    }
  }
}
