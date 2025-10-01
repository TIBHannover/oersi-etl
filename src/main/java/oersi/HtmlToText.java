/*
 * Copyright 2025 Tobias Bülte, hbz NRW
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oersi;

import org.metafacture.metafix.FixMethod;
import org.metafacture.metafix.Metafix;
import org.metafacture.metafix.Record;
import org.metafacture.metafix.api.FixFunction;

import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

/**
 * Turn html text to plain text. Can be used via Java Integration in fix.
 *
 * @author Tobias Bülte (tobiasNx)
 *
 */


public class HtmlToText implements FixFunction {

    public HtmlToText() {
    }
    @Override
    public void apply(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            record.transform(params.get(0), s -> Jsoup.parse(s).wholeText());
    }

}