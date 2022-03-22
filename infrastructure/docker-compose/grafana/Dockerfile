FROM grafana/grafana:8.2.3

COPY ./datasources/prometheus.yml /etc/grafana/provisioning/datasources/prometheus.yml
COPY ./dashboards/dashboards.yml /etc/grafana/provisioning/dashboards/dashboards.yml
COPY ./dashboards/micrometerv9-dashboard.json /var/lib/grafana/dashboards/micrometerv9-dashboard.json