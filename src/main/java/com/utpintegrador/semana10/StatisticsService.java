package com.utpintegrador.semana10;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);

    public Multimap<Integer, Stock> groupByFarmacia(List<Stock> stocks) {
        logger.info("Agrupando {} registros por farmacia_id", stocks.size());

        ImmutableListMultimap.Builder<Integer, Stock> builder = ImmutableListMultimap.builder();

        stocks.forEach(stock -> {
            if (stock.getFarmaciaId() != null) {
                builder.put(stock.getFarmaciaId(), stock);
            }
        });

        Multimap<Integer, Stock> groupedStocks = builder.build();

        logger.debug("Stock agrupado en {} farmacias", groupedStocks.keySet().size());
        return groupedStocks;
    }

    public Map<Integer, FarmaciaStats> calculateFarmaciaStatistics(List<Stock> stocks) {
        logger.info("Calculando estadísticas por farmacia_id");

        Multimap<Integer, Stock> grouped = groupByFarmacia(stocks);
        Map<Integer, FarmaciaStats> statistics = Maps.newHashMap();

        for (Integer farmaciaId : grouped.keySet()) {
            Collection<Stock> items = grouped.get(farmaciaId);
            FarmaciaStats stats = calculateStatsForFarmacia(farmaciaId, items);
            statistics.put(farmaciaId, stats);
        }

        return statistics;
    }

    private FarmaciaStats calculateStatsForFarmacia(Integer farmaciaId, Collection<Stock> stocks) {
        List<Stock> stockList = Lists.newArrayList(stocks);

        int count = stockList.size();
        double avgPrecio = stockList.stream().mapToDouble(Stock::getPrecio).average().orElse(0.0);
        int totalCantidad = stockList.stream().mapToInt(Stock::getCantidad).sum();

        Optional<Stock> productoMasCaro = stockList.stream()
                .max(Comparator.comparing(Stock::getPrecio));

        return new FarmaciaStats(farmaciaId, count, avgPrecio, totalCantidad, productoMasCaro.orElse(null));
    }

    public List<Stock> filterStock(List<Stock> stocks, Double minPrecio, Integer minCantidad, Integer farmaciaId) {
        logger.info("Filtrando stock con criterios: precio >= {}, cantidad >= {}, farmacia_id = {}",
                minPrecio, minCantidad, farmaciaId);

        List<Predicate<Stock>> filtros = Lists.newArrayList();

        if (minPrecio != null) {
            filtros.add(s -> s.getPrecio() >= minPrecio);
        }
        if (minCantidad != null) {
            filtros.add(s -> s.getCantidad() >= minCantidad);
        }
        if (farmaciaId != null) {
            filtros.add(s -> farmaciaId.equals(s.getFarmaciaId()));
        }

        return stocks.stream()
                .filter(s -> filtros.stream().allMatch(f -> f.apply(s)))
                .collect(Collectors.toList());
    }

    public List<Stock> getTopProductosPorPrecio(List<Stock> stocks, int topN) {
        logger.info("Obteniendo top {} productos por precio", topN);

        Ordering<Stock> porPrecioDesc = Ordering.natural()
                .nullsLast()
                .onResultOf(Stock::getPrecio)
                .reverse();

        return porPrecioDesc.sortedCopy(stocks)
                .stream()
                .limit(topN)
                .collect(Collectors.toList());
    }

    public StockSummary createSummary(List<Stock> stocks) {
        logger.info("Creando resumen general del stock");

        int total = stocks.size();
        double avgPrecio = stocks.stream().mapToDouble(Stock::getPrecio).average().orElse(0.0);
        int totalCantidad = stocks.stream().mapToInt(Stock::getCantidad).sum();

        ImmutableSet<Integer> farmacias = ImmutableSet.copyOf(
                stocks.stream().map(Stock::getFarmaciaId).collect(Collectors.toSet())
        );

        ImmutableMap<Integer, Long> conteoPorFarmacia = ImmutableMap.copyOf(
                stocks.stream().collect(Collectors.groupingBy(
                        Stock::getFarmaciaId, Collectors.counting()))
        );

        return new StockSummary(total, avgPrecio, totalCantidad, farmacias, conteoPorFarmacia);
    }

    public static class FarmaciaStats {

        private final Integer farmaciaId;
        private final int productoCount;
        private final double precioPromedio;
        private final int cantidadTotal;
        private final Stock productoMasCaro;

        public FarmaciaStats(Integer farmaciaId, int productoCount, double precioPromedio,
                int cantidadTotal, Stock productoMasCaro) {
            this.farmaciaId = farmaciaId;
            this.productoCount = productoCount;
            this.precioPromedio = precioPromedio;
            this.cantidadTotal = cantidadTotal;
            this.productoMasCaro = productoMasCaro;
        }

        public Integer getFarmaciaId() {
            return farmaciaId;
        }

        public int getProductoCount() {
            return productoCount;
        }

        public double getPrecioPromedio() {
            return precioPromedio;
        }

        public int getCantidadTotal() {
            return cantidadTotal;
        }

        public Stock getProductoMasCaro() {
            return productoMasCaro;
        }
    }

    public static class StockSummary {

        private final int totalProductos;
        private final double precioPromedio;
        private final int cantidadTotal;
        private final ImmutableSet<Integer> farmacias;
        private final ImmutableMap<Integer, Long> productosPorFarmacia;

        public StockSummary(int totalProductos, double precioPromedio, int cantidadTotal,
                ImmutableSet<Integer> farmacias,
                ImmutableMap<Integer, Long> productosPorFarmacia) {
            this.totalProductos = totalProductos;
            this.precioPromedio = precioPromedio;
            this.cantidadTotal = cantidadTotal;
            this.farmacias = farmacias;
            this.productosPorFarmacia = productosPorFarmacia;
        }

        public int getTotalProductos() {
            return totalProductos;
        }

        public double getPrecioPromedio() {
            return precioPromedio;
        }

        public int getCantidadTotal() {
            return cantidadTotal;
        }

        public ImmutableSet<Integer> getFarmacias() {
            return farmacias;
        }

        public ImmutableMap<Integer, Long> getProductosPorFarmacia() {
            return productosPorFarmacia;
        }
    }
}
