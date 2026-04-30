package ru.mirea.zhirkovaei.employeedb;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;

    private Button buttonAddHeroes;
    private Button buttonShowHeroes;
    private Button buttonUpdateHero;
    private Button buttonDeleteHero;

    private HeroDao heroDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.textViewResult);

        buttonAddHeroes = findViewById(R.id.buttonAddHeroes);
        buttonShowHeroes = findViewById(R.id.buttonShowHeroes);
        buttonUpdateHero = findViewById(R.id.buttonUpdateHero);
        buttonDeleteHero = findViewById(R.id.buttonDeleteHero);

        AppDatabase db = App.getInstance().getDatabase();
        heroDao = db.heroDao();

        buttonAddHeroes.setOnClickListener(view -> addHeroes());
        buttonShowHeroes.setOnClickListener(view -> showHeroes());
        buttonUpdateHero.setOnClickListener(view -> updateFirstHero());
        buttonDeleteHero.setOnClickListener(view -> deleteFirstHero());
    }

    private void addHeroes() {
        Hero hero1 = new Hero();
        hero1.name = "Человек-Молния";
        hero1.superpower = "Сверхскорость";
        hero1.powerLevel = 90;

        Hero hero2 = new Hero();
        hero2.name = "Ледяная Тень";
        hero2.superpower = "Управление льдом";
        hero2.powerLevel = 85;

        Hero hero3 = new Hero();
        hero3.name = "Кибер-Титан";
        hero3.superpower = "Кибернетическая сила";
        hero3.powerLevel = 95;

        heroDao.insert(hero1);
        heroDao.insert(hero2);
        heroDao.insert(hero3);

        Toast.makeText(this, "Супергерои добавлены в базу данных", Toast.LENGTH_SHORT).show();

        showHeroes();
    }

    private void showHeroes() {
        List<Hero> heroes = heroDao.getAll();

        if (heroes.isEmpty()) {
            textViewResult.setText("В базе данных пока нет супергероев");
            return;
        }

        StringBuilder builder = new StringBuilder();

        for (Hero hero : heroes) {
            builder.append("ID: ").append(hero.id).append("\n");
            builder.append("Имя: ").append(hero.name).append("\n");
            builder.append("Суперспособность: ").append(hero.superpower).append("\n");
            builder.append("Уровень силы: ").append(hero.powerLevel).append("\n");
            builder.append("-------------------------\n");
        }

        textViewResult.setText(builder.toString());
    }

    private void updateFirstHero() {
        List<Hero> heroes = heroDao.getAll();

        if (heroes.isEmpty()) {
            Toast.makeText(this, "Нет героев для обновления", Toast.LENGTH_SHORT).show();
            return;
        }

        Hero firstHero = heroes.get(0);

        firstHero.name = firstHero.name + " Обновлённый";
        firstHero.powerLevel = firstHero.powerLevel + 10;

        heroDao.update(firstHero);

        Toast.makeText(this, "Первый герой обновлён", Toast.LENGTH_SHORT).show();

        showHeroes();
    }

    private void deleteFirstHero() {
        List<Hero> heroes = heroDao.getAll();

        if (heroes.isEmpty()) {
            Toast.makeText(this, "Нет героев для удаления", Toast.LENGTH_SHORT).show();
            return;
        }

        Hero firstHero = heroes.get(0);

        heroDao.delete(firstHero);

        Toast.makeText(this, "Первый герой удалён", Toast.LENGTH_SHORT).show();

        showHeroes();
    }
}