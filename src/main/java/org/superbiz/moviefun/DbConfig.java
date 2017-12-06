package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.MoviesBean;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Bean
    public DataSource albumsDataSource(
            @Value("${moviefun.datasources.albums.url}") String url,
            @Value("${moviefun.datasources.albums.username}") String username,
            @Value("${moviefun.datasources.albums.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);

        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(dataSource);

        return hikariDataSource;
    }

    @Bean
    public DataSource moviesDataSource(
            @Value("${moviefun.datasources.movies.url}") String url,
            @Value("${moviefun.datasources.movies.username}") String username,
            @Value("${moviefun.datasources.movies.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);

        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(dataSource);

        return hikariDataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter createAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();

        adapter.setDatabase(Database.MYSQL);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        adapter.setGenerateDdl(true);

        return adapter;
    }

    @Bean
    @Qualifier("albumsEntityManager")
    public LocalContainerEntityManagerFactoryBean createAlbumsEntityManagerFactoryBean(DataSource albumsDataSource, HibernateJpaVendorAdapter adapter) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        factoryBean.setDataSource(albumsDataSource);
        factoryBean.setJpaVendorAdapter(adapter);
        factoryBean.setPackagesToScan(AlbumsBean.class.getPackage().getName());
        factoryBean.setPersistenceUnitName("persistentAlbums");

        return factoryBean;
    }

    @Bean
    @Qualifier("moviesEntityManager")
    public LocalContainerEntityManagerFactoryBean createMoviesEntityManagerFactoryBean(DataSource moviesDataSource, HibernateJpaVendorAdapter adapter) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        factoryBean.setDataSource(moviesDataSource);
        factoryBean.setJpaVendorAdapter(adapter);
        factoryBean.setPackagesToScan(MoviesBean.class.getPackage().getName());
        factoryBean.setPersistenceUnitName("persistentMovies");

        return factoryBean;
    }

    @Bean(name = "albumsTransactionManager")
    public PlatformTransactionManager createAlbumsTransactionManager(@Qualifier("albumsEntityManager") LocalContainerEntityManagerFactoryBean albumsEntityManager) {
        return new JpaTransactionManager(albumsEntityManager.getObject());
    }

    @Bean(name = "moviesTransactionManager")
    public PlatformTransactionManager createMoviesTransactionManager(@Qualifier("moviesEntityManager") LocalContainerEntityManagerFactoryBean moviesEntityManager) {
        return new JpaTransactionManager(moviesEntityManager.getObject());
    }
}
