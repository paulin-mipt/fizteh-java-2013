package ru.fizteh.fivt.students.nlevashov.factory;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Представляет интерфейс для создание экземпляров {@link ru.fizteh.fivt.storage.structured.TableProvider}.
 *
 * Предполагается, что реализация интерфейса фабрики будет иметь публичный конструктор без параметров.
 */
public class MyTableProviderFactory implements TableProviderFactory, AutoCloseable {

    private final ReentrantLock locker = new ReentrantLock(true);

    HashMap<String, MyTableProvider> providers;
    volatile boolean isClosed;

    public MyTableProviderFactory() {
        providers = new HashMap<>();
        isClosed = false;
    }

    /**
     * Возвращает объект для работы с базой данных.
     *
     * @param path Директория с файлами базы данных.
     * @return Объект для работы с базой данных, который будет работать в указанной директории.
     *
     * @throws IllegalArgumentException Если значение директории null или имеет недопустимое значение.
     * @throws java.io.IOException В случае ошибок ввода/вывода.
     */
    @Override
    public TableProvider create(String path) throws IOException {
        if (isClosed) {
            throw new IllegalStateException("TableProviderFactory.create: factory closed");
        }
        if ((path == null) || path.trim().isEmpty()) {
            throw new IllegalArgumentException("TableProviderFactory.create: dir is null");
        }

        Path p = Paths.get(System.getProperty("user.dir")).resolve(path).normalize();
        locker.lock();
        try {
            providers.put(p.toString(), new MyTableProvider(p));
        } finally {
            locker.unlock();
        }

        return providers.get(p.toString());
    }

    @Override
    public void close() {
        if (!isClosed) {
            for (Map.Entry<String, MyTableProvider> entry : providers.entrySet()) {
                entry.getValue().close();
            }
            isClosed = true;
        }
    }
}
