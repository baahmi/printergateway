/**
 * 
 */
package com.mvdb.tools.hp;

import java.io.InputStream;

/**
 * @author mvdb
 *
 */
public interface PGHandler {

    void handle(String target, InputStream scan);
}
