using System;
using System.Windows.Forms;
using System.Drawing;

namespace WebBrowser
{
    /// <summary>
    /// <c>Web Control</c> Class To Be Placed In Each Form
    /// </summary>
    public class WebControl : UserControl
    {
        private String currentAddress;
        private TextBox addressBar, response;
        private Connection conn;
        private Tab currentTab;

        public WebControl()
        {
            conn = new Connection();
            currentAddress = "";

            initaliseTextBox( ref addressBar, new Point( 0, 0 ), HorizontalAlignment.Center );
            addressBar.KeyDown += new KeyEventHandler( url_input_request );
            
            initaliseTextBox( ref response, new Point( 0, addressBar.Height + 5 ), HorizontalAlignment.Left );
            response.Multiline = true;
            response.WordWrap = true;
            response.ReadOnly = true;
            response.ScrollBars = ScrollBars.Both;
            response.Anchor = ( AnchorStyles.Top | AnchorStyles.Right | AnchorStyles.Left | AnchorStyles.Bottom );

            Controls.AddRange( new TextBox[] { addressBar, response } );
        }

        /// <summary>
        /// Initalise Tab Controller For 
        /// </summary>
        /// <param name="box"></param>
        /// <param name="point"></param>
        /// <param name="align"></param>
        private void initaliseTextBox( ref TextBox box, Point point, HorizontalAlignment align )
        {
            if ( point == null )
                throw new ArgumentException( "Argument Cannot Be Null" );
            box = new TextBox();
            box.Location = point;
            box.Width = Width;
            box.TextAlign = align;
            box.Anchor = ( AnchorStyles.Top | AnchorStyles.Right | AnchorStyles.Left );
        }

        /// <summary>
        /// Set The Current Tab Reference
        /// </summary>
        /// <param name="current"></param>
        public void setCurrentTab( Tab current )
        {
            if ( current == null )
                throw new ArgumentException( "Argument Cannot Be Null" );
            currentTab = current;
        }

        /// <summary>
        /// Get The Current Web Address
        /// </summary>
        /// <returns>Current Address</returns>
        public String getAddress()
        {
            return currentAddress;
        }

        /// <summary>
        /// Set The Current Web Address
        /// </summary>
        /// <param name="add"></param>
        public void setAddress( String add )
        {
            if ( add == null )
                throw new ArgumentException( "Argument Cannot Be Null" );
            addressBar.Text = add;
        }

        /// <summary>
        /// Called To Attempt All URL Requests
        /// </summary>
        /// <param name="address"></param>
        /// <param name="backForward"></param>
        public void goToAddress( String address, Boolean backForward )
        {
            if ( address == null )
                throw new ArgumentException( "Argument Cannot Be Null" );

            if ( address == "" )
            {
                response.Text = addressBar.Text = currentTab.Text = currentAddress = "";
            }
            else
            {
                Program parent = ( Program ) ( ( TabControl ) currentTab.Parent ).Parent;
                string[] responseText = conn.GET( address, "text/html" );

                currentAddress = addressBar.Text = address;
                response.Text = responseText[ 1 ];
                currentTab.Text = responseText[ 0 ];

                if ( responseText[0] != "Error" )
                {
                    if ( !backForward && ( parent.getIndex() != parent.historyList.Count - 1 ) ) // If Address Not From Back or Forward Buttons And Not On Final Index
                    {
                        parent.setIndex( parent.historyList.Count - 1 ); // Jump To End of History
                        currentTab.set_forward( false ); // Disable Forward
                    }
                    if ( !backForward )
                    {
                        if ( parent.historyList.Count > 2000 ) // If History List Getting Too Long
                        {
                            parent.historyList.RemoveRange( 0, 500 );
                            parent.setIndex( parent.getIndex() - 500 );
                        }
                        parent.historyList.Add( new string[] { responseText[ 0 ], currentAddress } ); // Add To History
                        parent.setIndex( parent.getIndex() + 1 );
                    }
                    if ( parent.getIndex() > 0 )
                    {
                        currentTab.set_back( true );
                    }
                }
                response.Focus();
            }
        }

        /// <summary>
        /// Event Handler For Requesting An Address
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void url_input_request( Object sender, KeyEventArgs e )
        {
            if ( e.KeyCode == Keys.Enter )
            {
                goToAddress( ( ( TextBox ) sender ).Text, false );
                e.SuppressKeyPress = true; // No Rejection Sound Heard
            }
        }
    }
}
