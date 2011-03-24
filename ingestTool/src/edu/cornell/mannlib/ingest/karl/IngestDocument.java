package edu.cornell.mannlib.ingest.karl;

/*
Copyright © 2003-2008 by the Cornell University and the Cornell
Research Foundation, Inc.  All Rights Reserved.

Permission to use, copy, modify and distribute any part of VITRO
("WORK") and its associated copyrights for educational, research and
non-profit purposes, without fee, and without a written agreement is
hereby granted, provided that the above copyright notice, this
paragraph and the following three paragraphs appear in all copies.

Those desiring to incorporate WORK into commercial products or use
WORK and its associated copyrights for commercial purposes should
contact the Cornell Center for Technology Enterprise and
Commercialization at 395 Pine Tree Road, Suite 310, Ithaca, NY 14850;
email:cctecconnect@cornell.edu; Tel: 607-254-4698; FAX: 607-254-5454
for a commercial license.

IN NO EVENT SHALL THE CORNELL RESEARCH FOUNDATION, INC. AND CORNELL
UNIVERSITY BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL,
INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING
OUT OF THE USE OF WORK AND ITS ASSOCIATED COPYRIGHTS, EVEN IF THE
CORNELL RESEARCH FOUNDATION, INC. AND CORNELL UNIVERSITY MAY HAVE BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  */


import java.io.InputStream;
import java.util.HashMap;

import edu.cornell.mannlib.ingest.karl.IngestAction;
import edu.cornell.mannlib.ingest.karl.IngestDocument;
import edu.cornell.mannlib.ingest.karl.IngestEntity;
import edu.cornell.mannlib.ingest.karl.IngestEntityProcessor;



/**
 * An IngestDocument is a generic representation of a document from which
 * IngestEntities can be parsed.  Different kinds of IngestDocument classes
 * allow XML files, tab-delimited databases and perhaps INI files to be
 * parsed generically.
 *
 * Every IngestDocument is assumed to be formatted in a way such that each
 * piece of data read from the document can be associated with a tag.  This
 * tag is matched against a map of tag strings to IngestAction objects
 * that specify what to do with the data that is associated with the tag.
 * This assumption is most easily recognized with an XML document, where
 * every piece of data is explicitly tagged; however, a "tag" is simply
 * an action-defining string, and as they are generated by the document
 * itself, they can be anything.
 *
 * In a tab-delimited database, for example, the first row is a set of
 * column headers.  The TabFileIngestDocument reads these into a "headers"
 * tag-list, which it returns in sequence every time a row is parsed.
 * However, since the tags are parsed hierarchially, the
 * TabFileIngestDocument also returns a "row" tag for each row.
 */
public abstract class IngestDocument
{
    /// Item used to handle ingest processing
    private IngestEntityProcessor myProcessor;

    /**
     * Initializes the document with a given processor
     */
    public IngestDocument( IngestEntityProcessor processor )
    {
        myProcessor = processor;
    }

    /**
     * Processes the entity using the ingest processor that was provided
     * to this document.  This can perform a range of options, from
     * writing to a database to simply outputting the information to a
     * file or the debug console.
     */
    protected void process( IngestEntity entity )
    {
        myProcessor.process( entity );
    }

    /**
     * Performs the ingesting of this document using the data stream
     */
    public abstract void ingest( InputStream data ) throws Exception;

    /**
     * Parses the document under the current data element with the given
     * actions.  This method is often invoked by actions as part of their
     * processing in order to obtain further information about nested
     * objects.
     * @param actions An index mapping document tags to actions
     */
    protected void parse( HashMap<String,IngestAction> actions ) throws Exception
    {
        // Holds the last tag that was obtained
        String tag = null;

        // Run through each tag in the document.  This method will return
        // null when the end of the tag under which this parse was begun
        // is completed.
        while( null != (tag = getNextTag( tag )) )
        {
            // Get an action from the current set
            IngestAction action = actions.get( tag );

            // Do the action if it was found
            if( action != null ) action.perform( this );
        }

        // Move back up
        endSection( null );
    }

    /**
     * Obtains the next tag in the file.
     * @param tagToClose The tag to close, if the document should get the
     *                   next member of the current element list, or 'null'
     *                   if the document should process sub-elements of the
     *                   current section.
     */
    public abstract String getNextTag( String tagToClose ) throws Exception;

    /**
     * Forcibly ends the section with the given name.  Most of the time,
     * this method is just passed 'null' in order to go up one level in
     * section processing.
     * @param tag The name of the section to close.  If this is 'null',
     *            the document will return to the parent element of the
     *            current list (but will not move the parent reference).
     *            If this is a valid name, the document will move upward
     *            until the tag with the given name is found and advanced.
     */
    public abstract void endSection( String tag ) throws Exception; // scan until we find </tag>

    /**
     * Reads the data associated with the current tag
     */
    public abstract String readField() throws Exception;

    /**
     * Reads the next field in the set, formats it using the given object, and adds
     * the returned information to the target property of the provided entity.
     */
    protected class ParseField implements IngestAction
    {
        /// The property to which this field will be attached
        String myTargetProperty;

        /// This is the target object for the data
        IngestEntity myEntity;

        /// How to format the data
        IngestFormatter myFormatter;

        /**
         * Initializes this class
         */
        public ParseField( String property, IngestEntity entity, IngestFormatter formatter )
        {
            myTargetProperty = property;
            myEntity = entity;
            myFormatter = formatter;
        }

        /**
         * Parses a field from the document and places it in the target entity
         * @param document The document from which to read the field
         */
        public void perform( IngestDocument document ) throws Exception
        {
            myEntity.addProperty( myTargetProperty, myFormatter.format( document.readField() ) );
        }
    }

}